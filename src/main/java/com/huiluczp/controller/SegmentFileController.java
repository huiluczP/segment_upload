package com.huiluczp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huiluczp.bean.SegmentFile;
import com.huiluczp.service.SegmentFileService;
import com.huiluczp.util.ReturnResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

// 主要实现check文件存在与否
// 上传分片
// 整合分片
@Controller
@RequestMapping("")
public class SegmentFileController {
    @Autowired
    SegmentFileService segmentFileService;

    @Autowired
    private ObjectMapper mapper;

    @Value("${file.save-path}")
    private String savePath;

    @RequestMapping("/index")
    public String index(){
        return "/index.html";
    }

    @RequestMapping("/upload")
    public String upload(){
        return "/pages/upload.html";
    }

    @RequestMapping("/uploadSegment")
    @ResponseBody
    // 主要方法流程
    // 上传文件需要从前端取分片序号和分片大小，因为切割是前端切滴，所以文件原始大小也要返回来
    // 剩余信息在service中计算
    // 首先确认是否存在该文件，不存在就放到数据库中新建
    // 之后对segmentIndex分别处理，存储分片文件（文件分片前端完成）
    // 简化情况，认为前端都是异步请求，并且分片是按顺序请求的，只有前面的index处理了才能处理后面的分片（在前端体现）
    // 这样当segmentIndex和总count相同时，获取结果
    public ReturnResult upLoadSegmentFile(MultipartFile file, String originFileName, Integer fileSize, Integer segmentIndex, Integer segmentSize, String key) throws JsonProcessingException{
        System.out.println("文件 " + originFileName + " 开始");
        // 查找是否存在，不存在就写入
        SegmentFile segmentFile = segmentFileService.checkSegmentFile(key);
        if(segmentFile==null){
            boolean writeSuccess = segmentFileService.createSegmentFile(originFileName, savePath, fileSize, segmentSize, key);
            if(!writeSuccess){
                // 写入失败，返回错误信息
                return new ReturnResult(false, "文件数据库记录创建失败");
            }
        }
        segmentFile = segmentFileService.checkSegmentFile(key);

        // 将当前分片存入
        boolean segmentWriteSuccess = segmentFileService.saveSegment(file, savePath, key, originFileName, segmentIndex);
        if(!segmentWriteSuccess)
            // 分片存储失败
            return new ReturnResult(false, "分片文件失败");
        segmentFile.setSegmentIndex(segmentIndex);
        boolean isUpdateSuccess = segmentFileService.updateSegmentFileInfo(segmentFile);
        if(!isUpdateSuccess)
            // 更新失败
            return new ReturnResult(false, "文件数据库记录更新失败");

        class deleteThread implements Runnable{
            @Override
            public void run() {
                try {
                    segmentFileService.deleteSegments(key);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        // 判断是否分片齐全，齐全则合并生成究极文件
        // 其实考虑这步会不会失败应该在数据库再加一个值，再说吧
        if(segmentIndex==segmentFile.getSegmentTotal()){
            boolean mergeSuccess = segmentFileService.mergeSegment(key);
            if(mergeSuccess) {
                // 另开线程去自旋删除
                new Thread(new deleteThread()).start();
                return new ReturnResult(true, mapper.writeValueAsString(segmentFile));
            }
            else
                return new ReturnResult(false, "文件合并失败");
        }
        return new ReturnResult(true, mapper.writeValueAsString(segmentFile));
    }

    @RequestMapping("/checkFile")
    @ResponseBody
    // 检查文件是否已经存在，且返回segment信息
    public ReturnResult checkFileExist(String key) throws JsonProcessingException {
        SegmentFile segmentFile = segmentFileService.checkSegmentFile(key);
        if(segmentFile==null)
            return new ReturnResult(false, "该文件未上传");
        else{
            // 转成json回去用
            String fileJson = mapper.writeValueAsString(segmentFile);
            return new ReturnResult(true, fileJson);
        }
    }
}
