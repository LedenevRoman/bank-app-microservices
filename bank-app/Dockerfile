FROM eclipse-temurin:11-jre-alpine
ARG JAR_FILE=bank-app-runner/target/bank-app-runner-0.0.1.jar
ENV bot.name=LedRomBankBot \
bot.token=6608761635:AAH3wZEdgj2ggnDgrmNFwQXTXn-kDasF2YQ \
datasource.password=P@ssword1 \
datasource.url=jdbc:mysql://192.168.2.182:3306/bank_app_db \
datasource.username=root \
jwt.secret=aloha

COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]