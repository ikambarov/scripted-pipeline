podyaml = '''
metadata:
  labels:
    run: docker
  name: docker
spec:
  containers:
  - args:
    - sleep
    - "1000000"
    image: docker:latest
    name: docker
    volumeMounts:
      - mountPath: /var/run/docker.sock
        name: docker-sock
  volumes:
    - name: docker-sock
      hostPath:
        path: /var/run/docker.sock
'''

podTemplate(cloud: 'kubernetes', label: 'docker', name: 'docker', namespace: 'jenkins', yaml: podyaml, showRawYaml: false) {
    node('docker'){
        container('docker'){
            stage("Test"){
                sh "docker ps"
            }   
        }
    }
}
