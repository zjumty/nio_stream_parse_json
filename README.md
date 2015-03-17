# nio_stream_parse_json
在NIO的环境里如何解析json
场景: 在一些nio的网络框架, 比如netty, mina等, 读取数据时异步的, 也就是不能通过InputStream.read来以阻塞的方式读取数据.
而大多数json的库, 如FastJSON, jackson等都只能解析一个阻塞式的InputStream. 

在nio网络框架里, 每当收到数据时会以事件的形式通知到你. 但是网络是一个变化无常的环境, 对方发送给你的一个完整的json字符串的字节流.
到了你这里可以被分成几个ByteBuffer. 甚至一个ByteBuffer里的数据可能包含多个json串.

例如:
```
第一个包: {"id":1, "name":"te
第二个包: rry", age:10}{"id":2, "n
第三个包: ame":"tom", age:15}{"i
```

这一片一片过来的数据要怎么组装为正常的json呢. 上面的例子应该正确解析为两个json对象,和一部分还不完整的数据. 这部分数据需要等到后续的数据到来以后才能解析.

这个项目是基于netty环境, 实现一个ByteToMessageDecoder, 把ByteBuf解析为User对象.
