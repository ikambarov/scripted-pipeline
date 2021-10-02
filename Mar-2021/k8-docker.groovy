def podname="docker"

def podtemplate="""
metadata:
  labels:
    run: $podname
spec:
  affinity:
    podAntiAffinity:
    requiredDuringSchedulingIgnoredDuringExecution:
    - labelSelector:
      matchExpressions:
      - key: component
        operator: In
        values:
        - jenkins-jenkins-master
      topologyKey: "kubernetes.io/hostname"
  containers:
  - image: docker
    name: $podname
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
"""

podTemplate(name: podname, label: podname, yaml: podtemplate, showRawYaml: false) {
    node(podname){
        stage('Check version'){
            container(podname){
                sh '''
                    docker version
                '''
            }           
        }
    }
}