def podtemplate = '''
apiVersion: v1
kind: Pod
metadata:
  labels:
    type: k8-tools
spec:
  serviceAccountName: k8-tools-sa
  containers:
  - image: ikambarov/k8-tools
    name: k8-tools
    args:
    - sleep
    - "100000"
'''

podTemplate(label: 'k8-tools', name: 'k8-tools', namespace: 'tools', yaml: podtemplate, showRawYaml: false) {
    node("k8-tools"){
        container("k8-tools"){
            stage("deploy"){
                sh '''
                    kubectl run flaskex-pod --image=ikambarov/flaskex -n default --dry-run=client -o yaml | kubectl apply -f - 
                '''
            }
        }
    }
}
