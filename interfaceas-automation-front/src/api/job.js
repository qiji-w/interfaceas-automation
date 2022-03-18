import request from '@/util/request'

export function getById(id) {
    return request({
      url: '/job/' + id,
      method: 'get'
    })
  }
export function create(data) {
  return request({
    url: '/job/',
    method: 'post',
    data:data
  })
}
export function modify(data) {
    return request({
      url: '/job/',
      method: 'put',
      data:data
    })
  }
export function remove(id) {
  return request({
    url: '/job/'+id,
    method: 'delete'
  })
}
export function start(id) {
  return request({
    url: '/job/start',
    method: 'get',
    params:{id:id}
  })
}
export function stop(id) {
  return request({
    url: '/job/stop',
    method: 'get',
    params:{id:id}
  })
}