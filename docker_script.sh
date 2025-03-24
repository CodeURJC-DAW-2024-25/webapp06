# Build container
docker build -t alvaro3517/webapp6 .

# Push image
docker push alvaro3517/webapp6

# Up compose
docker-compose -p daw up