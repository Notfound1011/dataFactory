mvn clean package -Dmaven.test.skip=true
docker buildx build  --platform=linux/amd64 -t  3.1.250.199:5000/datafactory:v1.0.9 .
docker push 3.1.250.199:5000/datafactory:v1.0.9