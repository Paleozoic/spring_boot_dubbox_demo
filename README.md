# spring_boot_dubbox_demo
该项目集成了：
- Spring Cloud Config Center
- dubbox
- Spring Boot Admin

# 步骤
- 将dubbo加入私库
- 将模块加进私库
- 针对spring-boot-dubbox-demo下的pom.xml，执行:
   * mvn clean
   * mvn compile
   * mvn package
- 本地启动zookeeper
- 启动命令
java -jar 10-ms-config-center/target/ms-config-center-1.0-SNAPSHOT.jar --spring.profiles.active=dev --server.port=20000
java -jar 20-ms-admin/target/ms-admin-1.0-SNAPSHOT.jar --spring.profiles.active=dev --server.port=20001
java -jar 30-ms-provider/target/ms-provider-1.0-SNAPSHOT.jar --spring.profiles.active=dev --server.port=20002
java -jar 31-ms-consumer/target/ms-consumer-1.0-SNAPSHOT.jar --spring.profiles.active=dev --server.port=20003

# 访问
REST：http://127.0.0.1:20002/test/hello
Consumer：http://127.0.0.1:20003/world
Consumer：http://127.0.0.1:20003/world2