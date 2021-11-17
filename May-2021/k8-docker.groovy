def podtemplate = '''
apiVersion: v1
kind: Pod
metadata:
  labels:
    type: docker
spec:
  containers:
  - image: docker
    name: docker
    volumeMounts:
    - mountPath: /var/run/docker.sock
      name: docker-sock
      type: File
    args:
    - sleep
    - "100000"
  volumes:
  - name: docker-sock
    hostPath:
      path: /var/run/docker.sock
'''

podTemplate(label: 'docker', name: 'docker', namespace: 'tools', yaml: podtemplate, showRawYaml: false) {
    node("docker"){
        container("docker"){
            stage("Test"){
                sh "docker version"
            }
        }
    }
}
