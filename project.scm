; Author: Jordan Randleman -- project.scm
; => Contains the Scheme logic to execute the game for COEN160's Final Project

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Additional Rule

; Cannot enter the same word twice in the same session


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; GUI Layout

; Text field (immutable): letters given to work with
; Text field (immutable): current score of the session
; Text field (mutable): enter a word using the given letters (validate only uses given letters & makes a valid word)
; Button: reset the session by resetting the score & usable letters


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Assumes given the following as Java 11 primitives:

; (gui-launch-window)                  ; launches the GUI window that will terminate the program upon exit
; (gui-launch-session current-letters) ; resets the available letters (to the argument) and the current score (to 0)
; (gui-get-input current-score)        ; updates score on GUI, and gets either the next word OR #f if reset button was pushed
; (dictionary-valid-word? word)        ; determine whether <word> is a valid word according to some dictionary


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Helper Procedures
(define (get-random-letter)
  (string-ref "ABCDEFGHIJKLMNOPQRSTUVWXYZ" 
              (remainder (floor (* (random) 1000)) 26)))


(define (has-vowel? str)
  (fold (lambda (acc ch) (or acc (string-contains str ch))) 
        #f
        (quote ("A" "E" "I" "O" "U" "Y"))))


(define (get-random-letters)
  (define letters 
    (let loop ((n 0) (str ""))
      (if (< n 10)
          (loop (+ n 1) (string-append str (get-random-letter)))
          str)))
  (if (has-vowel? letters)
      letters
      (string-append "E" (substring letters 1))))


(define (get-input current-score)
  (define word (gui-get-input current-score))
  (if word (string-upcase word) #f))


(define (valid-letters? current-letters word)
  (fold (lambda (acc ch) (and acc (string-contains current-letters ch))) 
        #t 
        (string-split word "")))


(define (new-word? seen-words word)
  (not (member word seen-words)))


(define (valid-word? current-letters seen-words word)
  (and (valid-letters? current-letters word) 
       (new-word? seen-words word) 
       (dictionary-valid-word? word)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Main Execution
(define (launch-game)
  (gui-launch-window)
  (let game-loop ((current-letters (get-random-letters)))
    (gui-launch-session current-letters)
    (let session-loop ((seen-words (quote ())) (current-score 0))
      (define input (get-input current-score))
      (if input
          (if (valid-word? current-letters seen-words input) 
              (session-loop (cons input seen-words) (+ current-score (string-length input)))
              (session-loop seen-words current-score))))
    (game-loop (get-random-letters))))

(launch-game)
