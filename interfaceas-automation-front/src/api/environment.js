
import request from '@/util/request'

export function queryByProjectId(projectId) {
  return request({
    url: '/environment/queryByProjectId',
    method: 'get',
    params:{projectId:projectId}
  })
}
export function getById(id) {
    return request({
      url: '/environment/' + id,
      method: 'get'
    })
  }
export function create(data) {
  return request({
    url: '/environment/',
    method: 'post',
    data:data
  })
}
export function modify(data) {
    return request({
      url: '/environment/',
      method: 'put',
      data:data
    })
  }
export function remove(id) {
  return request({
    url: '/environment/'+id,
    method: 'delete'
  })
}