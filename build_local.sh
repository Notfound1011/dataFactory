mvn clean package -Dmaven.test.skip=true
docker buildx build  --platform=linux/amd64 -t  localhost:5000/datafactory:v1.0.23 .
docker push  localhost:5000/datafactory:v1.0.23