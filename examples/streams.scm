; streams.scm
; => Demos the implmentation of streams in Scheme160 via macros
; => This file does not need any cmd-line arguments when being executed

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Implementing streams
(define-macro (scons expr)
  (list (quote cons) 
        (list (quote delay) (cadr expr)) 
        (list (quote delay) (car (cddr expr)))))

(define (scar spair) (force (car spair)))
(define (scdr spair) (force (cdr spair)))

(define (stream->list stream list-length) ; facilitates printing stream contents
  (if (>= list-length 0)
      (cons (scar stream) (stream->list (scdr stream) (- list-length 1)))
      (quote ())))

(define (stream-map f s)
  (if (null? s)
      (quote ())
      (scons (f (scar s))
             (stream-map f (scdr s)))))

(define (stream-filter ? s)
  (cond ((null? s) (quote ()))
        ((? (scar s)) (scons (scar s) (stream-filter ? (scdr s))))
        (else (stream-filter ? (scdr s)))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Generating Primes
(define (sieve int-stream)
  (scons 
    (scar int-stream)
    (sieve 
      (stream-filter 
        (lambda (n) (positive? (remainder n (scar int-stream))))
        (scdr int-stream)))))

(define int-stream-from-2
  (let loop ((n 2))
    (scons n (loop (+ n 1)))))

(define primes (sieve int-stream-from-2))

(display (stream->list primes 10))
(newline)


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Generating Fibonacci Numbers
(define fibs
  (let loop ((a 0) (b 1))
    (scons a (loop b (+ a b)))))

(display (stream->list fibs 10))
(newline)

