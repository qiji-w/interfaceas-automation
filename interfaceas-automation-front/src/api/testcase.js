import request from '@/util/request'

export function query(params) {
  return request({
    url: '/testcase/',
    method: 'get',
    params:params
  })
}
export function queryByProjectId(projectId) {
  return request({
    url: '/testcase/queryByProjectId',
    method: 'get',
    params:{projectId:projectId}
  })
}
export function getById(id) {
    return request({
      url: '/testcase/' + id,
      method: 'get'
    })
  }
export function create(data) {
  return request({
    url: '/testcase/',
    method: 'post',
    data:data
  })
}
export function modify(data) {
    return request({
      url: '/testcase/',
      method: 'put',
      data:data
    })
  }
export function remove(id) {
  return request({
    url: '/testcase/'+id,
    method: 'delete'
  })
}
export function run(data) {
  return request({
    url: '/testcase/run',
    method: 'post',
    data:data
  })
}
export function copy(data) {
  return request({
    url: '/testcase/copy',
    method: 'put',
    data:data
  })
}