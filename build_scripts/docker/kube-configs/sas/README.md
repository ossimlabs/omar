# K8S Configs for the (S)QS (A)VRO (S)tager -- SAS

To start a cluster of 3 SQS, AVRO, and Stager applictions using
the Kubernetes ReplicationController:

`$ kubectl create -f sas-rc.yaml`

To add the service with dynamic routing and load-balancing:

`$ kubectl create -f sas-svc.yaml`

You can use the OpenShift Wrapper as well:

`$ oc create -f sas-rc.yaml`
`$ oc create -f sas-svc.yaml`

**NOT COMPLETE**

