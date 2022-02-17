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
            stage('Pull Charts'){
                git 'https://github.com/ikambarov/flaskex-chart.git'
            }

            stage('Install'){
                sh """
                    helm upgrade --install myapp -n default . 
                """
            }
        }
    }
}