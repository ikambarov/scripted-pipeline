podyaml = '''
metadata:
  labels:
    run: k8s
  name: k8s
spec:
  serviceAccount: agent-sa
  containers:
  - args:
    - sleep
    - "1000000"
    image: ikambarov/k8-tools
    name: k8s
'''

podTemplate(cloud: 'kubernetes', label: 'k8s', name: 'k8s', namespace: 'jenkins', yaml: podyaml, showRawYaml: false) {
    node('k8s'){
        container('k8s'){
            stage('Deploy'){
                sh """
                    kubectl create deployment flaskex --image=ikambarov/flaskex --port=5000 -n default -o yaml --dry-run | kubectl apply -n default --force -f - 
                    
                    kubectl expose deployment flaskex --target-port=5000 --type=NodePort -n default  -o yaml --dry-run | kubectl apply -n default --force -f - 
                """
            }
        }
    }
}