```shell
export GH_USER=<your_user_name>
echo <your_token> >> GH_TOKEN.txt
sudo docker build --no-cache --progress=plain --build-arg GH_USER=$GH_USER --secret id=gh_token,src=GH_TOKEN.txt -f src/main/docker/Dockerfile.multistage -t ghcr.io/pagopa/mil-auth:latest .
```