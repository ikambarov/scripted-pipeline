def k8slabel = "jenkins-agent-1"

def slavePodTemplate="""
metadata:
  labels:
    k8s-label: ${k8slabel}
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
  - name: docker
    image: docker
    imagePullPolicy: IfNotPresent
    command:
    - sleep
    - "10000"
    volumeMounts:
      - mountPath: /var/run/docker.sock
        name: docker-sock
  serviceAccountName: default
  volumes:
    - name: docker-sock
      hostPath:
        path: /var/run/docker.sock

"""

podTemplate(name: k8slabel, label: k8slabel, yaml: slavePodTemplate, showRawYaml: false) {
  node(k8slabel){
      stage('Docker'){
        container("docker"){
          sh 'docker ps'
        }     
      }
  }
}
