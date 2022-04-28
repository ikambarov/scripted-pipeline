pod = '''apiVersion: v1
kind: Pod
metadata:
  labels:
    run: docker
  name: docker
spec:
  containers:
  - image: docker
    name: docker
    args:
    - sleep
    - "1000000"
    volumeMounts:
    - mountPath: /var/run/docker.sock
      name: docker-sock
      type: File
  volumes:
  - name: docker-sock
    hostPath:
      path: /var/run/docker.sock
'''

podTemplate(cloud: 'kubernetes', label: 'docker', name: 'docker', yaml: pod ) {
    node('docker'){
        container('docker'){
            stage("Pull Dockerfile"){
                git('https://github.com/ikambarov/Flaskex-docker.git')
            }

            withCredentials([usernamePassword(credentialsId: 'dockerhub', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                stage("Check docker Version"){
                    sh """
                        docker build -t $DOCKER_USERNAME/flaskex:latest .
                        docker images | grep $DOCKER_USERNAME/flaskex
                    """
                }

                stage("Push Image"){
                    sh """
                        docker login -u $DOCKER_USERNAME -p $DOCKER_PASSWORD
                        docker push ikambarov/flaskex:latest
                    """
                }
            } 
        }
    }
}
