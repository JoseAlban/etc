" for vimpager
"" do not pass through to console if buffer is smaller than height
let vimpager_passthrough = 0

" terminal-specific magic
let s:iterm   = exists('$ITERM_PROFILE') || exists('$ITERM_SESSION_ID') || filereadable(expand("~/.vim/.assume-iterm"))
let s:screen  = &term =~ 'screen'
let s:tmux    = exists('$TMUX')
let s:xterm   = &term =~ 'xterm'

function! s:EscapeEscapes(string)
  " double each <Esc>
  return substitute(a:string, "\<Esc>", "\<Esc>\<Esc>", "g")
endfunction

function! s:TmuxWrap(string)
  if strlen(a:string) == 0
    return ""
  end

  let tmux_begin  = "\<Esc>Ptmux;"
  let tmux_end    = "\<Esc>\\"

  return tmux_begin . s:EscapeEscapes(a:string) . tmux_end
endfunction

" change shape of cursor in insert mode in iTerm 2
if s:iterm
  let start_insert  = "\<Esc>]50;CursorShape=1\x7"
  let end_insert    = "\<Esc>]50;CursorShape=0\x7"

  if s:tmux
    let start_insert  = s:TmuxWrap(start_insert)
    let end_insert    = s:TmuxWrap(end_insert)
  endif

  let &t_SI = start_insert
  let &t_EI = end_insert
endif

set number
set ruler
set wrap!
hi LineNr ctermfg=darkgrey
" colorscheme elflord

" activates filetype detection
filetype plugin indent on
" allows you to deal with multiple unsaved buffers simultaneously without resorting to misusing tabs
set hidden
" just hit backspace without this one and see for yourself
set backspace=indent,eol,start
set ttyfast

" via yang
syntax on
" set textwidth=200
set incsearch
set hlsearch
hi Search cterm=NONE ctermfg=black ctermbg=red
set shiftwidth=2
set tabstop=2
set softtabstop=2
" set smartindent
set cindent
" enable mouse reporting:
set mouse=a
set list
set listchars=tab:>-
set backspace=2
" use macosx clipboard after yank / only vim >=7.4:
set clipboard=unnamed
set paste

" Various options
set ww=<,>,[,]               " Allow the right/left arrows to go to next and previous line
" set cursorline --- it lags too much with syntax highlighting eg ruby
set showcmd                  " show incomplete cmds down the bottom
set lazyredraw               " do not redraw while running macros
set ignorecase smartcase     " smart casing
set dir=/tmp                 " where to put the swap files
set ttimeoutlen=50
set expandtab

" autocmd BufNewFile,BufRead *.json set ft=javascript

" stop ex mode - but has made -q- slow
" nnoremap Q <nop>
" nnoremap q: <nop>

""" remap things
" allow quit via single keypress (Q)
map q :qa<CR>
" switch files
map \ :n<CR>
map <bar> :prev<CR>

" Commenting blocks of code.
autocmd FileType c,cpp,java,scala let b:comment_leader = '// '
autocmd FileType sh,ruby,python   let b:comment_leader = '# '
autocmd FileType conf,fstab       let b:comment_leader = '# '
autocmd FileType tex              let b:comment_leader = '% '
autocmd FileType mail             let b:comment_leader = '> '
autocmd FileType vim              let b:comment_leader = '" '
noremap <silent> ,cc :<C-B>silent <C-E>s/^/<C-R>=escape(b:comment_leader,'\/')<CR>/<CR>:nohlsearch<CR>
noremap <silent> ,cu :<C-B>silent <C-E>s/^\V<C-R>=escape(b:comment_leader,'\/')<CR>//e<CR>:nohlsearch<CR>

" Replace pound with hash, so easier to input hash
noremap £ :norm i#<CR>

" Remove ASCII color code
noremap <silent> ,rc :%! gsed -r "s,\x1B\[[0-9;]*[a-zA-Z],,g"<CR>

