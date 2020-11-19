def k8slabel = "jenkins-agent-${UUID.randomUUID().toString()}"

def slavePodTemplate = """
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
        - name: terraform
          image: hashicorp/terraform:0.13.5
          imagePullPolicy: IfNotPresent
          command:
          - sleep
          - "10000"
        serviceAccountName: terraform
    """

podTemplate(name: k8slabel, label: k8slabel, yaml: slavePodTemplate, showRawYaml: false) {
    node(k8slabel) {
        container("terraform") {
            stage("Pull Repo"){
                git branch: 'update#2', url: 'https://github.com/ikambarov/k8-terraform-pod.git'
            }

            stage("Terraform Init") {
                sh 'terraform init'
            }

            stage("Terraform Apply") {
                sh 'terraform apply -auto-approve'
            }
        }
    }
}
