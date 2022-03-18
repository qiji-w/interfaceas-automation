import request from '@/util/request'

export function query(params) {
  return request({
    url: '/module/',
    method: 'get',
    params:params
  })
}
export function queryByProjectId(projectId) {
  return request({
    url: '/module/queryByProjectId',
    method: 'get',
    params:{projectId:projectId}
  })
}
export function getById(id) {
    return request({
      url: '/module/' + id,
      method: 'get'
    })
  }
export function create(data) {
  return request({
    url: '/module/',
    method: 'post',
    data:data
  })
}
export function modify(data) {
    return request({
      url: '/module/',
      method: 'put',
      data:data
    })
  }
export function remove(id) {
  return request({
    url: '/module/'+id,
    method: 'delete'
  })
}