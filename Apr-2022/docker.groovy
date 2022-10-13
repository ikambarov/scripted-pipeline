template = '''
apiVersion: v1
kind: Pod
metadata:
  name: docker
spec:
  volumes:
  - name: docker
    hostPath:
      path: /var/run/docker.sock
  containers:
  - args:
    - sleep
    - "100000"
    image: docker
    name: docker
    volumeMounts:
    - mountPath: /var/run/docker.sock
      name: docker
    '''

podTemplate(cloud: 'kubernetes', label: 'docker', showRawYaml: false, yaml: template) {
    node("docker"){
        container("docker"){
            stage("Pull repo"){
                git "https://github.com/ikambarov/docker-melodi.git"
            }

            withCredentials([usernamePassword(credentialsId: 'dockerhub', passwordVariable: 'DOCKERHUBPASS', usernameVariable: 'DOCKERHUBUSER')]) {
                stage("Build"){
                    sh '''
                        docker build -t $DOCKERHUBUSER/melodi:v1 .
                        docker images | grep melodi
                    '''
                }

                stage("Push"){
                    sh '''
                        docker login -u $DOCKERHUBUSER -p $DOCKERHUBPASS
                        docker push $DOCKERHUBUSER/melodi:v1
                    '''
                }
            }
        }
    }
}