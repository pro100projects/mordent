FROM public.ecr.aws/amazoncorretto/amazoncorretto:17.0.5-al2

COPY ./build/libs/*.jar /app.jar

ENTRYPOINT ["java", "-Xms512m", "-Xmx1024m", "-jar", "/app.jar"]