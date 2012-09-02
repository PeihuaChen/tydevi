#!/bin/sh

./tydevi.sh -help

./tydevi.sh
./tydevi.sh "Example isn't another way to teach, it's the only way to teach" "Colorless green ideas sleep furiously."
./tydevi.sh -frames "Example isn't another way to teach, it's the only way to teach" "Colorless green ideas sleep furiously."
./tydevi.sh -in testsent.txt
./tydevi.sh -factory ALL "A quick brown fox jumped over the lazy dog"
./tydevi.sh -factory CCPROCESSED "A quick brown fox jumped over the lazy dog"

./tydevi.sh -out input
./tydevi.sh -out example "Example isn't another way to teach, it's the only way to teach" "Colorless green ideas sleep furiously."
./tydevi.sh -out testsent -in testsent.txt
