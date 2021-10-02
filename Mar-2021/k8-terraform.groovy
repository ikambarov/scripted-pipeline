def podname="terraform"

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
  - image: hashicorp/terraform:0.14.11
    name: $podname
"""

podTemplate(name: podname, label: podname, yaml: podtemplate, showRawYaml: false) {
    node(podname){
        stage('Check version'){
            container(podname){
                sh '''
                    terraform version
                '''
            }           
        }
    }
}