
import vueCookies from 'vue-cookies'

const currentUserKey = 'current-user'  
const expiredTime = "2h" 

export function setCookie(key,value){
  vueCookies.set(key,value,expiredTime)
}
export function getCookie(key){
  return vueCookies.get(key)
}
export function removeCookie(key){
  vueCookies.remove(key)
}

export function getCurrentUser() {
  return getCookie(currentUserKey)
}
export function setCurrentUser(value) {
  setCookie(currentUserKey, value)
}
export function removeCurrentUser() {
  removeCookie(currentUserKey)
}