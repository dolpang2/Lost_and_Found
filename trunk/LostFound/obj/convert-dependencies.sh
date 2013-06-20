#!/bin/sh
# AUTO-GENERATED FILE, DO NOT EDIT!
if [ -f $1.org ]; then
  sed -e 's!^C:/cygwin_package/lib!/usr/lib!ig;s! C:/cygwin_package/lib! /usr/lib!ig;s!^C:/cygwin_package/bin!/usr/bin!ig;s! C:/cygwin_package/bin! /usr/bin!ig;s!^C:/cygwin_package/!/!ig;s! C:/cygwin_package/! /!ig;s!^D:!/cygdrive/d!ig;s! D:! /cygdrive/d!ig;s!^C:!/cygdrive/c!ig;s! C:! /cygdrive/c!ig;' $1.org > $1 && rm -f $1.org
fi
