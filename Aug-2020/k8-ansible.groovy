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
  - name: ansible
    image: ikambarov/ansible
    imagePullPolicy: IfNotPresent
    command:
    - sleep
    - "10000"
  serviceAccountName: default
"""

podTemplate(name: k8slabel, label: k8slabel, yaml: slavePodTemplate, showRawYaml: false) {
  node(k8slabel){
      stage('Ansible'){
        container("ansible"){
          sh 'ansible --version'
        }     
      }
  }
}
