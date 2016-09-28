#!/bin/bash

O2_APPS=($(ls -d o2-*))
O2_APPS+=("tlv")

export O2_APPS
export TAG="latest"

