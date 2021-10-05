def podname="k8-tools"

def podtemplate="""
metadata:
  labels:
    run: $podname
spec:
  serviceAccountName: jenkins
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
  - image: ikambarov/k8-tools
    name: $podname
    args:
    - sleep
    - "100000"
"""

podTemplate(name: podname, label: podname, yaml: podtemplate, showRawYaml: false) {
    node(podname){
        stage('Check version'){
            container(podname){
                sh '''
                    kubectl run nginx-pod --image=nginx --dry-run -o yaml | kubectl apply -f -
                '''
            }           
        }
    }
}