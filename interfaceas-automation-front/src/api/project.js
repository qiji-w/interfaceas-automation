import request from '@/util/request'

export function queryAll() {
  return request({
    url: '/project/queryAll',
    method: 'get'
  })
}
export function getById(id) {
    return request({
      url: '/project/' + id,
      method: 'get'
    })
  }
export function create(data) {
  return request({
    url: '/project/',
    method: 'post',
    data:data
  })
}
export function modify(data) {
    return request({
      url: '/project/',
      method: 'put',
      data:data
    })
  }
export function remove(id) {
  return request({
    url: '/project/'+id,
    method: 'delete'
  })
}