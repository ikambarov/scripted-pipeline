properties([
    parameters([
        string(description: 'Provide Linux VM IP', name: 'IPADDRESS', trim: true)
        ])
    ])
    
node("worker1"){
    withCredentials([sshUserPrivateKey(credentialsId: 'masterkey', keyFileVariable: 'SSHKEY', usernameVariable: 'SSHUSERNAME')]) {     
        stage("Install Python, Git"){
            sh """
                ssh -o StrictHostKeyChecking=no -i $SSHKEY $SSHUSERNAME@${params.IPADDRESS} 'yum install epel-release -y'
                ssh -o StrictHostKeyChecking=no -i $SSHKEY $SSHUSERNAME@${params.IPADDRESS} 'yum install python3 git -y'
            """
        }
        stage("Pull code"){
            sh """
                ssh -o StrictHostKeyChecking=no -i $SSHKEY $SSHUSERNAME@${params.IPADDRESS} 'rm -rf Flaskex'
                ssh -o StrictHostKeyChecking=no -i $SSHKEY $SSHUSERNAME@${params.IPADDRESS} 'git clone https://github.com/anfederico/Flaskex'
            """
        }
        stage("Install Requirements"){
            sh """
                ssh -o StrictHostKeyChecking=no -i $SSHKEY $SSHUSERNAME@${params.IPADDRESS} 'pip3 install -r Flaskex/requirements.txt'
            """
        }
        stage("Start App"){
            sh """
                ssh -o StrictHostKeyChecking=no -i $SSHKEY $SSHUSERNAME@${params.IPADDRESS} 'if pgrep python3; then pkill -9 python3; fi'
                ssh -o StrictHostKeyChecking=no -i $SSHKEY $SSHUSERNAME@${params.IPADDRESS} 'nohup python3 Flaskex/app.py &'
            """
        }
    }
}