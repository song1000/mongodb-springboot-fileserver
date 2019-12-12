# MongoDB File Server（基于 MongoDB 的文件服务器）

声名： 基于waylau：mongodb-file-server进行的改造，用户可以存储大于16Mb的文件，也可以自定义buckit

MongoDB File Server is a file server system based on MongoDB. MongoDB File Server is committed to the storage of small files, such as pictures in the blog, ordinary documents and so on.

It's using some very popular technology like:

* MongoDB 3.6.4
* Spring Boot 2.0.3.RELEASE
* Spring Data MongoDB 2.0.8.RELEASE
* Spring 5.0.7.RELEASE
* Thymeleaf 3.0.9.RELEASE
* Thymeleaf Layout Dialect 2.2.0
* Embedded MongoDB 2.0.2
* Gradle 4.5.1

基于 MongoDB 的文件服务器。MongoDB File Server 致力于文件的存储，比如博客中图片、普通文档等。由于MongoDB 支持多种数据格式的存储，对于二进制的存储自然也是不话下，所以可以很方便的用于存储文件。由于  MongoDB 的 BSON 文档对于数据量大小的限制（每个文档不超过16M），所以本文件服务器存储文档根据大小（16M  ）采用不同的存储方式。




## Features 特性

* Easy to use.（易于使用）
* RESTful API.
* Chinese characters friendly.（中文友好）
* ...

## APIS

Here are useful APIs.

* GET  /files/{pageIndex}/{pageSize} : Paging query file list.(分页查询文件列表)
* GET  /files/{id} : Download file.(下载某个文件)
* GET  /view/{id} : View file online.(在线预览某个文件。比如，显示图片)
* POST /upload : Upload file.(上传文件)
* DELETE /{id} : Delete file.(删除文件)
