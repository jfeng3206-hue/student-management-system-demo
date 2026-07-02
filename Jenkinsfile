pipeline {
    agent any

    options {
        skipStagesAfterUnstable()
        timestamps()
    }

    stages {
        stage('Build') {
            steps {
                sh '''
                    export PATH=/opt/java/openjdk/bin:$PATH
                    chmod +x ./mvnw
                    ./mvnw -DskipTests package
                '''
            }
        }

        stage('Deploy to EC2') {
            steps {
                sh '''
                    EC2_HOST=34.221.51.27
                    EC2_USER=ubuntu
                    SSH_KEY=/var/jenkins_home/springboot-demo.pem
                    JAR_FILE=target/demo-0.0.1-SNAPSHOT.jar

                    scp -i "$SSH_KEY" -o StrictHostKeyChecking=no "$JAR_FILE" "$EC2_USER@$EC2_HOST:/home/ubuntu/app.jar.tmp"

                    ssh -i "$SSH_KEY" -o StrictHostKeyChecking=no "$EC2_USER@$EC2_HOST" '
                        set -e

                        sudo docker stop sms || true
                        sudo docker rm sms || true

                        sudo pkill -f "^java -jar app.jar$" || true
                        sleep 3

                        mv /home/ubuntu/app.jar.tmp /home/ubuntu/app.jar
                        cd /home/ubuntu

                        set -a
                        . /home/ubuntu/sms.env
                        set +a

                        nohup java -jar app.jar > app.log 2>&1 &

                        sleep 15
                        curl -f -X POST "http://localhost:8080/v1/name/aggregation?name=Jenkins"
                    '
                '''
            }
        }
    }
}
