import request from '@/util/request'

export function query(params) {
  return request({
    url: '/task/',
    method: 'get',
    params:params
  })
}
export function queryByProjectId(projectId) {
  return request({
    url: '/task/queryByProjectId',
    method: 'get',
    params:{projectId:projectId}
  })
}

export function queryDetailByProjectId(projectId) {
  return request({
    url: '/task/queryDetailByProjectId',
    method: 'get',
    params:{projectId:projectId}
  })
}

export function getById(id) {
    return request({
      url: '/task/' + id,
      method: 'get'
    })
  }
export function create(data) {
  return request({
    url: '/task/',
    method: 'post',
    data:data
  })
}
export function modify(data) {
  return request({
    url: '/task/',
    method: 'put',
    data:data
  })
}
export function remove(id) {
  return request({
    url: '/task/'+id,
    method: 'delete'
  })
}
export function getModulesByTaskId(taskId) {
  return request({
    url: '/task/getModulesByTaskId',
    method: 'get',
    params:{taskId:taskId}
  })
}

export function run(data) {
  return request({
    url: '/task/run',
    method: 'post',
    data:data
  })
}
export function archive(id) {
  return request({
    url: '/task/archive',
    method: 'get',
    params:{id:id}
  })
}
export function changeIsJobOrNot(taskId,isJob) {
  return request({
    url: '/task/changeIsJobOrNot',
    method: 'post',
    data:{taskId:taskId,isJob:isJob}
  })
}