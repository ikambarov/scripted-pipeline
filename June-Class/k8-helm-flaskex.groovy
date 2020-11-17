
def pod_name =  "jenkins-agent-${UUID.randomUUID().toString()}"

def pod_yaml = '''
apiVersion: v1
kind: Pod
metadata:
  labels:
    run: jenkins-agent
  name: jenkins-agent
spec:
  affinity:
    podAntiAffinity:
      requiredDuringSchedulingIgnoredDuringExecution:
      - labelSelector:
        matchExpressions:
        - key: app.kubernetes.io/component
          operator: In
          values:
          - jenkins-master
        topologyKey: "kubernetes.io/hostname"
  containers:
  - image: ikambarov/jnlp-agent-k8-tools
    name: k8-tools 
  serviceAccount: k8-tools
'''

podTemplate(label: pod_name, name: pod_name, namespace: 'default', yaml: pod_yaml) {
    node(pod_name){
        container('k8-tools'){
            stage("Pull Repo"){
                git url: 'https://github.com/ikambarov/flaskex-chart.git'
            }

            stage("Helm Install"){
                sh '''
                    helm upgrade --install myapp .
                '''
            }
        }
    }
}