
const currentUserProject = 'current-user-project'  //key

export function set(key,value){
    window.localStorage.setItem(key,JSON.stringify(value))
}
export function get(key){
    return JSON.parse(window.localStorage.getItem(key))
}
export function remove(key){
    window.localStorage.removeItem(key)
}

export function getCurrentProject() {
  return get(currentUserProject)
}
export function setCurrentProject(value) {
  set(currentUserProject, value)
}
export function removeCurrentProject() {
  remove(currentUserProject)
}