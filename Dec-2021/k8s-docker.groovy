podTemplate = '''apiVersion: v1
kind: Pod
metadata:
  labels:
    agent: docker
  name: docker
spec:
  containers:
  - image: docker
    name: docker
    args:
    - sleep
    - "100000"
    volumeMounts:
    - mountPath: /var/run/docker.sock
      name: docker-sock
      type: File
  volumes:
  - name: docker-sock
    hostPath:
      path: /var/run/docker.sock
'''

podTemplate(cloud: 'kubernetes', label: 'docker', showRawYaml: false, yaml: podTemplate) {
    node('docker'){
        container('docker'){
            stage("Pull repo"){
                git 'https://github.com/ikambarov/Flaskex-docker.git'
            }

            withCredentials([usernamePassword(credentialsId: 'docker-creds', passwordVariable: 'DOCKERHUB_PASSWORD', usernameVariable: 'DOCKERHUB_USERNAME')]) {
                stage("Build"){
                    sh 'docker build -t $DOCKERHUB_USERNAME/flaskex .'
                }

                stage("Push"){
                    sh '''
                        docker login -u $DOCKERHUB_USERNAME -p $DOCKERHUB_PASSWORD
                        docker push $DOCKERHUB_USERNAME/flaskex
                    '''
                }
            }
        }
    }
}

