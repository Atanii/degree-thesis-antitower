#!/bin/bash

# removes comments
phase_one='s/\/\/.\+\/\///g'

# "optimize" text for processing
phase_two='s/\(||\)//g;s/\n//g;s/\s|\t|\n//g;s/;;/;/g'

# Need to save?
if [ "$2" = "-s" ];
then
    cat $1 | sed -e $phase_one "$1" | sed -z -E $phase_two > $3
else
    cat $1 | sed -e $phase_one "$1" | sed -z -E $phase_two
fi
