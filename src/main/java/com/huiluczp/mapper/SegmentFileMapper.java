package com.huiluczp.mapper;

import com.huiluczp.bean.SegmentFile;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SegmentFileMapper {

    // 获取对应的分片文件实体类
    @Select("select * from segment_file where md5_key = #{key}")
    @Results(id="segmentFileResult",value={
            @Result(id=true, column = "id",property = "id"),
            @Result(column = "file_path",property = "filePath"),
            @Result(column = "file_name",property = "fileName"),
            @Result(column = "size",property = "size"),
            @Result(column = "segment_index",property = "segmentIndex"),
            @Result(column = "segment_size",property = "segmentSize"),
            @Result(column = "segment_total",property = "segmentTotal"),
            @Result(column = "md5_key",property = "md5Key")
    })
    public List<SegmentFile> getSegmentFileByKey(String key);

    // 添加对应的文件实体类
    @Insert("insert into segment_file(id,file_path,file_name," +
            "size,segment_index,segment_size,segment_total,md5_key) " +
            "values(#{id},#{filePath},#{fileName},#{size},#{segmentIndex}," +
            "#{segmentSize},#{segmentTotal},#{md5Key})")
    public int insertSegmentFile(SegmentFile segmentFile);

    // 主要用来更新分片信息
    @Update({"update segment_file set " +
            "file_path = #{filePath},file_name = #{fileName},size = #{size}," +
            "segment_index = #{segmentIndex}, segment_size = #{segmentSize}," +
            "segment_total = #{segmentTotal}, md5_key = #{md5Key}" +
            "where id = #{id}" })
    public int updateSegmentFile(SegmentFile segmentFile);
}
