package main

import "gorm.io/gorm"

type Todo struct {
	gorm.Model
	Title    string `json:"title"`
	Complete bool   `json:"complete"`
}
