# segment_upload
File fragment upload example created by spring boot</br>
Springboot框架下实现文件分片上传功能，实现断点续传和秒传。
1.	首先利用数据库存储文件信息，包括文件物理地址，分片接收进程和对应的md5码。
2.	前端ajax获取文件存在与否的信息，几种情况：</br>
    (1)不存在，则创建数据库记录，成功后调用分片1的上传。</br>
    (2)存在，Index和总分片数量相同，秒传成功显示结果。</br>
    (3)存在，但index小于总分片数量，调用分片index的上传。
3.	分片在前端根据分片Index计算起点末尾，slice切割，ajax调用上传传到服务器并存储。</br>
    当前分片传递成功，ajax接收success信息，串行递归进行index+1的分片的上传。</br>

持久化框架使用MyBatis，表创建文件已上传。</br>

项目说明:</br>
[Springboot实现文件分片断点续传功能（后端篇）](https://blog.csdn.net/qq_41733192/article/details/123782427)</br>
[Springboot实现文件分片断点续传功能（前端篇）](https://blog.csdn.net/qq_41733192/article/details/123783292)</br>

