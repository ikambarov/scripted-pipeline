def k8slabel = "jenkins-agent"

def slavePodTemplate = """
      metadata:
        labels:
          k8s-label: ${k8slabel}
        annotations:
          jenkinsjoblabel: ${env.JOB_NAME}-${env.BUILD_NUMBER}
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
        - name: k8-tools
          image: ikambarov/k8-tools
          imagePullPolicy: Always
        serviceAccountName: default
    """

podTemplate(name: k8slabel, label: k8slabel, yaml: slavePodTemplate, showRawYaml: false) {
    node(k8slabel) {
        stage("Kubernetes") {
            container("k8-tools") {
                sh 'kubectl version'
                sh 'helm version'
            }
        }
    }
}
