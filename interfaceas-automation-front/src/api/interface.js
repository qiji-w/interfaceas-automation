import request from '@/util/request'

export function query(params) {
  return request({
    url: '/interface/',
    method: 'get',
    params:params
  })
}
export function queryByProjectId(projectId) {
  return request({
    url: '/interface/queryByProjectId',
    method: 'get',
    params:{projectId:projectId}
  })
}
export function getById(id) {
    return request({
      url: '/interface/' + id,
      method: 'get'
    })
  }
export function create(data) {
  return request({
    url: '/interface/',
    method: 'post',
    data:data
  })
}
export function modify(data) {
    return request({
      url: '/interface/',
      method: 'put',
      data:data
    })
  }
export function remove(id) {
  return request({
    url: '/interface/'+id,
    method: 'delete'
  })
}