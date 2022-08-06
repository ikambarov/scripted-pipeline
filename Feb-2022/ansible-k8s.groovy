properties([
    parameters([
        string(description: 'Enter Linux IP Address', name: 'IP', trim: true)
        ])
    ])

podtemplate = '''apiVersion: v1
kind: Pod
metadata:
  labels:
    run: ansible
  name: ansible
spec:
  containers:
  - args:
    - sleep
    - "10000000"
    image: ikambarov/ansible
    name: ansible'''

podTemplate(cloud: 'kubernetes', label: 'ansible', showRawYaml: false, yaml: podtemplate ) {
    node('ansible'){
        stage("Test"){
            container('ansible'){
                withCredentials([sshUserPrivateKey(credentialsId: 'ansible-key', keyFileVariable: 'SSH_KEY', usernameVariable: 'SSH_USERNAME')]) {
                    sh """
                        export ANSIBLE_HOST_KEY_CHECKING=False
                        ansible -m ping --private-key=$SSH_KEY all -i \"${params.IP},\" -u $SSH_USERNAME 
                    """
                }
            }
        }
    }
}

