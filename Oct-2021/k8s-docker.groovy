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
        stage("Check docker Version"){
            container('docker'){
                sh "docker build "
            }
        }
    }
}
