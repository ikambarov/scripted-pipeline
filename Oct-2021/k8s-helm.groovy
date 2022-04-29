pod = '''apiVersion: v1
kind: Pod
metadata:
  labels:
    run: helm
  name: helm
spec:
  containers:
  - image: ikambarov/k8s-tools
    name: helm
  serviceAccountName: agent-sa
'''

podTemplate(cloud: 'kubernetes', showRawYaml: false, label: 'helm', name: 'helm', yaml: pod ) {
    node('helm'){
        container('helm'){
            stage("Pull Chart"){
                git("https://github.com/ikambarov/flaskex-chart.git")
            }

            stage("Test"){
                sh """
                    helm upgrade --install -n default flaskex . 
                """
            }
        }
    }
}
