#! /bin/zsh

# set -x # enable to detect where shell is spending time

# Path for cd to search; used to need to explicitly set "." for the cdmatch
# function (and retained for backward compatibility).
export CDPATH=.:\
$HOME/workspace

# shell defaults override
LESS_OPTS=-MsrRix8K
LSCOLORCODES=Cxgxfxhxbxabefacagxgxc
export LESS=$LESS_OPTS
export EDITOR=vim
# export DIFFTOOL=sublimerge-diff-wrapper
export LSCOLORS=$LSCOLORCODES

# homebrew
BREW_HOME=$(/usr/local/bin/brew --prefix)

# path
export PATH=\
$HOME/bin:\
$BREW_HOME/sbin:\
$BREW_HOME/bin:\
$HOME/.pulumi/bin:\
$PATH # "$path[@]"

# python
eval "$(pyenv init -)"
