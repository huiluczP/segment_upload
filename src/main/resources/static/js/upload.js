// 控制文件分片和上传
// 不要忘记控制前端的显示结果
// 简单尝试直接使用串行

var key = ''
var segmentIndex = 0
var segmentSize = 2 * 1024 * 1024;  // 先2MB用着

// 文件key计算
function calFileKey(file){
    //把文件的信息存储为一个字符串
    var filedetails= file.name + file.size + file.type + file.lastModifiedDate;
    //使用当前文件的信息用md5加密生成一个key
    var key = hex_md5(filedetails);
    var key10 = parseInt(key,16);
    //把加密的信息 转为一个62位的
    var key62 = Tool._10to62(key10);
    console.log("cal key:" + key62)
    return key62
}

// 计算分片数量
// 注意分片序号从1开始
function calTotalSegmentSize(file){
    var size = file.size
    var segmentTotal = Math.ceil(size / segmentSize)
    return segmentTotal;
}

// 计算分片的开始
function calSegmentStartAndEnd(segmentIndex, file){
    var start = (segmentIndex - 1) * segmentSize;
    var end = Math.min(start + segmentSize, file.size);
    return new Array(start, end);
}

// 检测当前文件是否存在，存在且完成上传则输出秒传信息
// 存在但未完成，则将upload的segmentIndex修改，等待后续上传（把前端信息也修改一下）
// 不存在则md5码(key)，等待后续上传（把前端信息也修改一下）
function checkFile(){
    var file = $('#filename').get(0).files[0]
    key = calFileKey(file)
    $('#md5').html('md5_key: ' + key)
    console.log(file.name)

    // ajax请求找下数据库中该文件是否存在
    $.ajax({
        url:"/checkFile",
        type:"post",
        cache: false,
        data: {
            'key': key
        },
        dataType: 'json',
        success:function(data){
            var result = data.success
            if(!result){
                $('#uuid').html('uuid_name:')
                $('#output').html('该文件未上传')
            }else{
                var segmentFile = JSON.parse(data.message)
                var segmentIndexNow = segmentFile.segmentIndex
                var segmentTotal = segmentFile.segmentTotal
                var uuid = segmentFile.fileName
                $('#uuid').html('uuid_name: ' + uuid)
                if(segmentIndexNow==segmentTotal){
                    // 完成上传
                    $('#output').html('该文件已完成上传，别再传了')
                }else{
                    $('#output').html(segmentIndexNow + '/' +segmentTotal)
                    segmentIndex = segmentIndexNow + 1
                }
            }
        },
        error:function(){
            $('#output').html("check请求错误")
            console.log("check请求错误")
        }
    })
}


// 总的上传方法，中间递归上传分片
function upload(){
    var file = $('#filename').get(0).files[0]
    key = calFileKey(file)
    $('#md5').html('md5_key:' + key)

    // ajax请求找下数据库中该文件是否存在
    $.ajax({
        url:"/checkFile",
        type:"post",
        cache: false,
        data: {
            'key': key
        },
        dataType: 'json',
        success:function(data){
            var result = data.success
            if(!result){
                var segmentIndexNow = 0
                var segmentTotal = calTotalSegmentSize(file)
                $('#uuid').html('uuid_name:')
                $('#output').html(segmentIndexNow + '/' +segmentTotal)
                var segmentIndex = segmentIndexNow + 1
                // 开始上传分片
                uploadSegment(segmentIndex, file, key)
            }else{
                var segmentFile = JSON.parse(data.message)
                var segmentIndexNow = segmentFile.segmentIndex
                var segmentTotal = segmentFile.segmentTotal
                var uuid = segmentFile.fileName
                $('#uuid').html('uuid_name: ' + uuid)
                if(segmentIndexNow==segmentTotal){
                    // 完成上传
                    $('#output').html('该文件已完成上传，别再传了')
                }else{
                    $('#output').html(segmentIndexNow + '/' +segmentTotal)
                    var segmentIndex = segmentIndexNow + 1
                    // 开始上传分片
                    uploadSegment(segmentIndex, file, key)
                }
            }
        },
        error:function(){
            console.log("check请求错误")
        }
    })
}


// 上传分片
function uploadSegment(segmentIndex, file, key){
    var fd = new FormData();
    var segmentIndex = segmentIndex;
    var sAe = calSegmentStartAndEnd(segmentIndex, file)
    var segmentStart = sAe[0]
    var segmentEnd = sAe[1]
    var segment = file.slice(segmentStart, segmentEnd)
    var segmentTotal = calTotalSegmentSize(file)
    var originFileName = file.name

    fd.append('file', segment)
    fd.append('fileSize', file.size)
    fd.append('segmentIndex', segmentIndex)
    fd.append('key', key)
    fd.append('segmentSize', segmentSize)
    fd.append('originFileName', originFileName)

    $.ajax({
        url:"/uploadSegment",
        type:"post",
        cache: false,
        data:fd,
        processData: false,
        contentType: false,
        success:function(data){
            var result = data.success
            if(!result){
                $('#output').html(data.message)
            }else{
                var segmentFile = JSON.parse(data.message)
                var uuid = segmentFile.fileName
                $('#uuid').html('uuid_name: ' + uuid)
                // 递归调用
                $('#output').html(segmentIndex + "/" + segmentTotal)
                if(segmentIndex < segmentTotal)
                    uploadSegment(segmentIndex+ 1, file, key)
            }
        },error:function(){
            console.log("分片" + segmentIndex + "上传失败")
        }
    })
}