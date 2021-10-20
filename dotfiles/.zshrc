#!/bin/bash
# ^ just for shellcheck - actually zsh

tput cup "$LINES" # move prompt to bottom just once

##########AUTOGEN by omzsh#########
# Path to your oh-my-zsh installation.
export ZSH=$HOME/.oh-my-zsh

# Set name of the theme to load.
# Look in ~/.oh-my-zsh/themes/
# Optionally, if you set this to "random", it'll load a random theme each
# time that oh-my-zsh is loaded.
# ZSH_THEME="random"

# Uncomment the following line to use case-sensitive completion.
# CASE_SENSITIVE="true"

# Uncomment the following line to use hyphen-insensitive completion. Case
# sensitive completion must be off. _ and - will be interchangeable.
HYPHEN_INSENSITIVE="true"

# Uncomment the following line to disable bi-weekly auto-update checks.
DISABLE_AUTO_UPDATE="true"

# Uncomment the following line to change how often to auto-update (in days).
# export UPDATE_ZSH_DAYS=13

# Uncomment the following line to disable colors in ls.
# DISABLE_LS_COLORS="true"

# Uncomment the following line to disable auto-setting terminal title.
# DISABLE_AUTO_TITLE="true"

# Uncomment the following line to enable command auto-correction.
# ENABLE_CORRECTION="true"

# Uncomment the following line to display red dots whilst waiting for completion.
COMPLETION_WAITING_DOTS="true"

# Uncomment the following line if you want to disable marking untracked files
# under VCS as dirty. This makes repository status check for large repositories
# much, much faster.
# DISABLE_UNTRACKED_FILES_DIRTY="true"

# Uncomment the following line if you want to change the command execution time
# stamp shown in the history command output.
# The optional three formats: "mm/dd/yyyy"|"dd.mm.yyyy"|"yyyy-mm-dd"
HIST_STAMPS="mm/dd/yyyy"

# Would you like to use another custom folder than $ZSH/custom?
# ZSH_CUSTOM=/path/to/new-custom-folder

# Which plugins would you like to load? (plugins can be found in ~/.oh-my-zsh/plugins/*)
# Custom plugins may be added to ~/.oh-my-zsh/custom/plugins/
# Example format: plugins=(rails git textmate ruby lighthouse)
# Add wisely, as too many plugins slow down shell startup.
plugins=(git zsh-syntax-highlighting zsh-completions zsh-autosuggestions osx brew aws python docker)
# removed plugins: bundler(doesnt allow using gems not defined in the gemfile) ruby pip

# User configuration

# export PATH="/usr/bin:/bin:/usr/sbin:/sbin:/usr/local/bin:/opt/X11/bin:/usr/texbin"
# export MANPATH="/usr/local/man:$MANPATH"

source $ZSH/oh-my-zsh.sh

# You may need to manually set your language environment
# export LANG=en_US.UTF-8

# Preferred editor for local and remote sessions
# if [[ -n $SSH_CONNECTION ]]; then
#   export EDITOR='vim'
# else
#   export EDITOR='mvim'
# fi

# Compilation flags
# export ARCHFLAGS="-arch x86_64"

# ssh
# export SSH_KEY_PATH="~/.ssh/dsa_id"

# Set personal aliases, overriding those provided by oh-my-zsh libs,
# plugins, and themes. Aliases can be placed here, though oh-my-zsh
# users are encouraged to define aliases within the ZSH_CUSTOM folder.
# For a full list of active aliases, run `alias`.
#
# Example aliases
# alias zshconfig="mate ~/.zshrc"
# alias ohmyzsh="mate ~/.oh-my-zsh"

##### OVERRIDES ####
unalias run-help
autoload run-help
HELPDIR=/usr/local/share/zsh/help

# Completions
fpath=($HOME/bin/zsh_completions/ $fpath)
autoload -U +X compinit && compinit
autoload -U +X bashcompinit && bashcompinit
autoload -U +X colors && colors

# autoload -U promptinit zmv
# promptinit

## things that are assumed to be something else should not be autocompleted
compdef -d play

# kubernetes
if command -v kubectl >/dev/null
then
  source <(kubectl completion zsh)
fi

# options ; man zshoptions
unsetopt NO_MATCH # dont try to expand on every command; fixes 'zsh: no matches found'
setopt RC_EXPAND_PARAM # expand like txt${a b c}file.txt
setopt EXTENDED_GLOB # e.g. cp ^*.(tar|bz2|gz)
setopt RM_STAR_WAIT
setopt PUSHD_SILENT
setopt PUSHD_TO_HOME
setopt NO_CLOBBER # prevents accidentally overwriting an existing file.
# setopt NOHUP # do not kill background jobs on logout.
setopt MULTIOS
setopt LONG_LIST_JOBS

bindkey "\ea" beginning-of-line
bindkey "\ee" end-of-line
bindkey "\ex" expand-word
bindkey "\ew" kill-whole-line
bindkey "\ef" emacs-forward-word
bindkey "\eu" undo
bindkey "\ei" vi-swap-case
bindkey "\eq" quote-region
bindkey "\eq" quote-region
bindkey -s '\e3' '#'
bindkey "^[[1;3C" forward-word # alt-right
bindkey "^[[1;3D" backward-word # alt-left
bindkey "^[[1;3A" beginning-of-line # alt-up
bindkey "^[[1;3B" end-of-line # alt-down
bindkey -s '\e\\' '|'
bindkey -s "\e'" '|'

# source ~/.git.zsh # git prompt
# PS1="%{%(?.$fg[white].$fg[red])%}%*%{$fg[blue]%}%1~%{$reset_color%} "
# PROMPT="%{${vcs_info_msg_0_}$fg[blue]%}%1~%{$reset_color%} "
# hash_dir() {
#   echo -e "$(dirname "$(pwd)" | shasum | cut -c1-5)"
# }
# PROMPT='%{$fg[yellow]%}$(hash_dir) %1~%{$reset_color%} '
# RPS1="%{$fg[white]%}%*%{$reset_color%}"
PROMPT='%{$fg[yellow]%}\$ %{$reset_color%}'

source ~/.fzf.zsh # fzf completion on C-t and C-r

# here instead of .zprofile, because my aliases were being overwritten >:/
source ~/.neutral-profile

# Must be here, otherwise /Users/josealban/.oh-my-zsh/lib/misc.zsh:13> export PAGER=less ; would set it
export PAGER=most

# minikube autocompletion
#hash minikube && source <(minikube completion zsh)

# Babylon specific
# source "$HOME/workspace/babylon-bin/share/shipcat/shipcat.complete.sh"
# prompt_k8s() {
#   local ctx
#   if ctx=$(kubectl config current-context 2> /dev/null); then
#     echo -e "(%{$fg[green]%}${ctx}%{$reset_color%})"
#   fi
# }
# if command -v kubectl >/dev/null
# then
#   PROMPT_K8='$(prompt_k8s)'
#   PS1="$PROMPT_K8 ${PS1}"
# fi
