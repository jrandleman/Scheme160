;; Macros to define custom special forms in Scheme160
;;   => Hardcoded in ../Util/Runtime as a series of ".append" statements

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Implementing Boolean AND:  (and <obj> ...)
(define-macro (and expr)
  (fold (lambda (acc item) (list (quote if) acc item #f))
        #t
        (cdr expr)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Implementing Boolean OR: (or <obj> ...)
(define-macro (or expr)
  (fold-right (lambda (item acc)
                (list (list (quote lambda) (list (quote scm160:or-value))
                            (list (quote if) (quote scm160:or-value) (quote scm160:or-value) acc))
                      item))
              #f
              (cdr expr)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Implementing DELAY: (delay <obj>)
(define-macro (delay expr)
  (list (quote lambda) (quote ()) (cadr expr)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Implementing COND: (cond (<condition> <expr> ...) ...) (cond (<condition> <expr> ...) ... (else <expr> ...))
(define-macro (cond expr)
  (define (make-condition c) (if (eq? c (quote else)) #t c))
  (define (make-consequence c) (cons (quote begin) c))
  (fold-right (lambda (clause acc)
                (list (quote if) (make-condition (car clause))
                      (make-consequence (cdr clause))
                      acc))
              (quote (if #f #f)) ; innermost expr yields a <void> value
              (cdr expr)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Implementing LET: (let ((<variable> <value>) ...) <body> ...) (let <procedure-name> ((<argument> <initial-value>) ...) <body> ...)
(define-macro (let expr)
  (define (get-params let-bindings) (map car let-bindings))
  (define (get-args let-bindings) (map cadr let-bindings))
  (define (get-body let-body) (cons (quote begin) let-body))
  (define (generate-anon-let)
    (cons (list (quote lambda) (get-params (cadr expr))
                (get-body (cddr expr)))
          (get-args (cadr expr))))
  (define (generate-named-let)
    (list (list (quote lambda) (quote ())
            (list (quote begin)
              (list (quote define) (cadr expr)
                    (list (quote lambda) (get-params (car (cddr expr)))
                          (get-body (cdr (cddr expr)))))
              (cons (cadr expr) (get-args (car (cddr expr))))))))
  (if (symbol? (cadr expr))
      (generate-named-let)
      (generate-anon-let)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Implementing QUASIQUOTE: (quasiquote <obj>) (unquote <obj>) (unquote-splicing <obj>)
(define (scm160:quasiquote:tagged-list? obj tag)
  (and (eq? (car obj) tag) (not (null? (cdr obj)))))

(define (scm160:quasiquote->quote lst level)
  (define (iter lst)
    (define hd (if (not (atom? lst)) (car lst)))
          ; finished parsing expression (proper list)
    (cond ((null? lst) (quote ()))
          ; finished parsing expression (dotted list)
          ((atom? lst)
            (list (list (quote quote) lst)))
          ; unquote rest of list
          ((scm160:quasiquote:tagged-list? lst (quote unquote))
            (if (= level 0)
                (list (cadr lst))
                (list (list (quote list) (quote (quote unquote)) (scm160:quasiquote->quote (cadr lst) (- level 1)))))) ; *there*: recursively parse, in nested quasiquote
          ; quote atom
          ((atom? hd)
            (cons (list (quote list) (list (quote quote) hd))
                  (iter (cdr lst))))
          ; unquote datum
          ((scm160:quasiquote:tagged-list? hd (quote unquote))
            (if (= level 0)
                (cons (list (quote list) (cadr hd))
                      (iter (cdr lst)))
                (cons (list (quote list) (scm160:quasiquote->quote hd level)) ; recursively parse, in nested quasiquote (level will be decremented *there*)
                      (iter (cdr lst)))))
          ; unquote & signal should splice element
          ((scm160:quasiquote:tagged-list? hd (quote unquote-splicing))
            (if (= level 0)
                (cons (cadr hd) ; evaluate datum & append to the expression
                      (iter (cdr lst)))
                (cons (list (quote list) (scm160:quasiquote->quote hd (- level 1))) ; recursively parse, in nested quasiquote
                      (iter (cdr lst)))))
          ; nested quasiquote
          ((scm160:quasiquote:tagged-list? hd (quote quasiquote))
            (cons (list (quote list) (scm160:quasiquote->quote hd (+ level 1))) ; recursively parse, in nested quasiquote
                  (iter (cdr lst))))
          ; quasiquote expression
          (else
            (cons (list (quote list) (scm160:quasiquote->quote hd level))
                  (iter (cdr lst))))))
  (cons (quote append) (iter lst)))

(define-macro (quasiquote expr)
  (scm160:quasiquote->quote (cadr expr) 0))
