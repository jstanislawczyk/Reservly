#!/bin/bash
# This script is used for restarting our dev server deployed in AWS EC2 instance

ec2User=someEc2UserName
ec2InstanceIp=0.0.0.0
privateKeyFileName=SomeKey.pem

chmod 400 SomeKey.pem

ssh $ec2User@$ec2InstanceIp -i $privateKeyFileName -T << 'EOSSH'
    reservlyServerPid=`pidof java`

    numbers='^[0-9]+$'

    if ! [[ $reservlyServerPid =~ $numbers ]] ; then
        echo "Starting server"
    else
        echo "Restarting server"
        sudo kill $reservlyServerPid
    fi

    cd ../../tmp/reservly-2.7.x/bin/
    sudo ./reservly &
EOSSH