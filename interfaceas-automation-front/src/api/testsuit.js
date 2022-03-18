import request from '@/util/request'

export function query(params) {
  return request({
    url: '/testsuit/',
    method: 'get',
    params:params
  })
}
export function queryByProjectId(projectId) {
  return request({
    url: '/testsuit/queryByProjectId',
    method: 'get',
    params:{projectId:projectId}
  })
}
export function getById(id) {
    return request({
      url: '/testsuit/' + id,
      method: 'get'
    })
  }
export function create(data) {
  return request({
    url: '/testsuit/',
    method: 'post',
    data:data
  })
}
export function modify(data) {
    return request({
      url: '/testsuit/',
      method: 'put',
      data:data
    })
  }
export function remove(id) {
  return request({
    url: '/testsuit/'+id,
    method: 'delete'
  })
}