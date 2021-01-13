app.service('brandService',function ($http) {
    this.findPage = function (page,rows) {
        return $http.get("../brand/findPage.do?page=" + page + "&rows=" + rows);
    }

    this.save = function (methodName,pojo) {
        return $http.post('../brand/' + methodName + '.do',pojo);
    }

    this.findOne = function (id) {
        return $http.get("../brand/findById.do?id=" + id);
    }

    this.del = function (selectIds) {
        return $http.get("../brand/remove.do?ids=" + selectIds);
    }

    this.search = function (page,rows,searchEntity) {
       return  $http.post("../brand/search.do?page=" + page + "&rows=" + rows,searchEntity);
    }

    this.selectOptionList = function (){
        return $http.get("../brand/selectOptionList.do");
    }
});